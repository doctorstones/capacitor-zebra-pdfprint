package com.infoservicenet.plugins.zebrapdf;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "ZebraPdfPrint",
permissions = {
                @Permission(strings = {Manifest.permission.BLUETOOTH}, alias = "BLUETOOTH"),
                @Permission(strings = {Manifest.permission.BLUETOOTH_ADMIN}, alias = "BLUETOOTH_ADMIN"),
                @Permission(strings = {Manifest.permission.ACCESS_COARSE_LOCATION}, alias = "ACCESS_COARSE_LOCATION"),
                @Permission(strings = {Manifest.permission.ACCESS_FINE_LOCATION}, alias = "ACCESS_FINE_LOCATION")
        }
)
public class ZebraPdfPrintPlugin extends Plugin {

    private ZebraPdfPrint implementation = new ZebraPdfPrint();

    private static final String LOGDEBUG = "INFOSERVICE_LOG";
    static final String lock = "ZebraLock";

    private String filePath = null;
    private Integer fileWidth = 0;

    private ArrayList<DiscoveredPrinter> discoveredPrinters;
    private String macAddress;
    private com.zebra.sdk.comm.Connection printerConnection;
    private com.zebra.sdk.printer.ZebraPrinter printer;

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", implementation.echo(value));
        call.resolve(ret);
    }

    /**
     * Search all BT Devices (not only Zebra printers):
     * useful when ZEBRA DISCOVERER API don't work;
     *
     * @param call
     */
    @PluginMethod()
    public void searchDevices(PluginCall call) {
        JSArray printers = this.BluetoothGenericDiscovery();
        JSObject ret = new JSObject();
        ret.put("printers", printers);
        call.resolve(ret);
    }

    /**
     * Search Zebra Printers by Zebra API;
     *
     * @param call
     */
    @PluginMethod
    public void searchPrinters(PluginCall call) {

        saveCall(call);

        if (!hasRequiredPermissions()) {
            pluginRequestAllPermissions();
        }

        bridge.triggerWindowJSEvent("called_searchPrinters", "{ 'dataKey': 'dataValue' }");
        Log.i(LOGDEBUG, "HERE A");
        discoveredPrinters = new ArrayList<DiscoveredPrinter>();

        DiscoveryHandler discoveryHandler = new DiscoveryHandler() {
            @Override
            public void foundPrinter(DiscoveredPrinter discoveredPrinter) {
                Log.i(LOGDEBUG, "Discovered a printer");
                discoveredPrinters.add(discoveredPrinter);
            }

            @Override
            public void discoveryFinished() {
                Log.i(LOGDEBUG, "Discovery finished");
                JSObject ret = new JSObject();
                ret.put("value", discoveredPrinters);

                PluginCall savedCall = getSavedCall();
                savedCall.resolve(ret);
            }

            @Override
            public void discoveryError(String s) {
                Log.i(LOGDEBUG, "Discovery error " + s);
                PluginCall savedCall = getSavedCall();
                //savedCall.resolve(ret);
                savedCall.reject(s);

            }
        };

        try {
            Log.i(LOGDEBUG, "HERE B");
            BluetoothDiscoverer.findPrinters(getActivity(), discoveryHandler);
        } catch (ConnectionException e) {

            call.reject("ConnectionException", e);
            Log.i(LOGDEBUG, "Printer connection error");

        }

    }

    /**
     * Private func called by "searchDevices" Plugin Method.
     *
     * @return
     */
    private JSArray BluetoothGenericDiscovery() {

        JSArray printers = new JSArray();

        try {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            Set<BluetoothDevice> devices = adapter.getBondedDevices();

            for (Iterator<BluetoothDevice> it = devices.iterator(); it.hasNext(); ) {
                BluetoothDevice device = it.next();
                String name = device.getName();
                String mac = device.getAddress();

                JSObject p = new JSObject();
                p.put("name", name);
                p.put("address", mac);
                Log.i(LOGDEBUG, "Discovered a device: " + name);
                printers.put(p);

            }
        } catch (Exception e) {
            Log.i(LOGDEBUG, e.getMessage());
            System.err.println(e.getMessage());
        }
        return printers;
    }

    @PluginMethod()
    public void isConnected(PluginCall call) {
        JSObject ret = new JSObject();
        ret.put("connected", this.isConnected());
        Log.i(LOGDEBUG, "Printer - isConnected" + this.isConnected());
        call.resolve(ret);
    }

    private boolean isConnected() {
        return printerConnection != null && printerConnection.isConnected();
    }

    @PluginMethod()
    public void connect(PluginCall call) {
        String address = call.getString("MACAddress");
        com.zebra.sdk.printer.ZebraPrinter printer = this.connect(address);
        JSObject ret = new JSObject();
        ret.put("success", printer != null);
        call.success(ret);
    }

    private com.zebra.sdk.printer.ZebraPrinter connect(String macAddress) {
        if (isConnected()) disconnect();
        printerConnection = null;
        this.macAddress = macAddress;
        printerConnection = new BluetoothConnection(macAddress);
        synchronized (ZebraPdfPrint.lock) {
            try {
                printerConnection.open();
            } catch (ConnectionException e) {
                Log.i(LOGDEBUG, "Printer - Failed to open connection", e);
                disconnect();
            }
            printer = null;
            if (printerConnection.isConnected()) {
                try {
                    printer = ZebraPrinterFactory.getInstance(printerConnection);
                    PrinterLanguage pl = printer.getPrinterControlLanguage();
                } catch (ConnectionException e) {
                    Log.i(LOGDEBUG, "Printer - Error...", e);
                    printer = null;
                    disconnect();
                } catch (ZebraPrinterLanguageUnknownException e) {
                    Log.i(LOGDEBUG, "Printer - Unknown Printer Language", e);
                    printer = null;
                    disconnect();
                }
            }
        }
        return printer;
    }

    @PluginMethod()
    public void disconnect(PluginCall call) {
        this.disconnect();
        call.success();
    }

    private void disconnect() {
        synchronized (ZebraPdfPrint.lock) {
            try {
                if (printerConnection != null) {
                    printerConnection.close();
                }
            } catch (ConnectionException e) {
                e.printStackTrace();
            }
        }
    }

    @PluginMethod()
    public void printerStatus(PluginCall call) {
        String address = call.getString("MACAddress");
        JSObject ret = new JSObject();
        // Log.i(LOGDEBUG, "Printer macAddress by local var: "+this.macAddress); 
        // Log.i(LOGDEBUG, "Printer macAddress passed: "+address);
        // Log.i(LOGDEBUG, "Printer connected: "+this.isConnected());
        if (this.isConnected()) { // Prima di chiamare printer status, bisogna chiamare connect;
            // if(this.macAddress == address && this.isConnected()){ // Prima di chiamare printer status, bisogna chiamare connect;
            try {
                PrinterStatus status = printer.getCurrentStatus();
                ret.put("isReadyToPrint", status.isReadyToPrint);
                ret.put("isPaused", status.isPaused);
                ret.put("isReceiveBufferFull", status.isReceiveBufferFull);
                ret.put("isRibbonOut", status.isRibbonOut);
                ret.put("isPaperOut", status.isPaperOut);
                ret.put("isHeadTooHot", status.isHeadTooHot);
                ret.put("isHeadOpen", status.isHeadOpen);
                ret.put("isHeadCold", status.isHeadCold);
                ret.put("isPartialFormatInProgress", status.isPartialFormatInProgress);

                DiscoveredPrinterBluetooth dp = new DiscoveredPrinterBluetooth(address, "");

                Map<String, String> discoveryMap = dp.getDiscoveryDataMap();
                discoveryMap.put("LINK_OS_MAJOR_VER", SGD.GET("appl.link_os_version", printerConnection));
                discoveryMap.put("PRODUCT_NAME", SGD.GET("device.product_name", printerConnection));
                discoveryMap.put("SYSTEM_NAME", SGD.GET("bluetooth.friendly_name", printerConnection));
                discoveryMap.put("HARDWARE_ADDRESS", SGD.GET("bluetooth.address", printerConnection));
                discoveryMap.put("FIRMWARE_VER", SGD.GET("appl.name", printerConnection));
                discoveryMap.put("SERIAL_NUMBER", SGD.GET("device.unique_id", printerConnection));

                ret.put("PRODUCT_NAME", dp.getDiscoveryDataMap().get("PRODUCT_NAME"));
                ret.put("SYSTEM_NAME", dp.getDiscoveryDataMap().get("SYSTEM_NAME"));
                ret.put("HARDWARE_ADDRESS", dp.getDiscoveryDataMap().get("HARDWARE_ADDRESS"));
                ret.put("FIRMWARE_VER", dp.getDiscoveryDataMap().get("FIRMWARE_VER"));
                ret.put("SERIAL_NUMBER", dp.getDiscoveryDataMap().get("SERIAL_NUMBER"));
                ret.put("isPDFEnabled", isPDFEnabled(printerConnection));

                // for (String settingsKey : dp.getDiscoveryDataMap().keySet()) {
                //     // System.out.println("Key: " + settingsKey + " Value: " + dp.getDiscoveryDataMap().get(settingsKey));
                //     ret.put(settingsKey, dp.getDiscoveryDataMap().get(settingsKey)); 
                // }

                // if (printerConnection instanceof BluetoothConnection) {
                //     String friendlyName = ((BluetoothConnection) printerConnection).getFriendlyName();
                //     ret.put("friendlyName",friendlyName);
                //     String manufacturer = ((BluetoothConnection) printerConnection).getManufacturer();
                //     ret.put("manufacturer",manufacturer);
                // }

            } catch (Exception ex) {
                //Ignore
            }
            ret.put("connected", true);
        } else {
            ret.put("connected", false);
        }
        call.success(ret);
    }

    /**
     * Print:
     *
     * @param call
     */

    @PluginMethod()
    public void printZPL(PluginCall call) {
        String message = call.getString("cpcl");
        if (!isConnected()) {
            call.error("Printer Not Connected");
        } else {
            if (this.printZPL(message)) {
                call.success();
            } else {
                call.error("unknown error");
            }
        }
    }

    private boolean printZPL(String cpcl) {
        try {
            if (!isConnected()) {
                Log.i(LOGDEBUG, "Printer Not Connected");
                return false;
            }

            byte[] configLabel = cpcl.getBytes();
            printerConnection.write(configLabel);

            // try {
            //     Thread.sleep(500);
            // } catch(InterruptedException e) {
            // }

            // if (printerConnection instanceof BluetoothConnection) {
            //     String friendlyName = ((BluetoothConnection) printerConnection).getFriendlyName();
            //     System.out.println(friendlyName);
            // }

        } catch (ConnectionException e) {
            Log.i(LOGDEBUG, "Error Printing", e);
            return false;
        }
        return true;
    }

    @PluginMethod()
    public void printPDF(PluginCall call) {

        Uri uri = Uri.parse(call.getString("uri"));
        this.filePath = uri.getPath();
        try {
            this.fileWidth = getPageWidth(uri);
        } catch (Exception e) {
            // e.printStackTrace();
        }

        //Log.i(LOGDEBUG, "fileWidth:"+fileWidth);

        if (!isConnected()) {
            call.error("Printer Not Connected");
        } else {
            this.sendPrint(this.macAddress);
            call.success();

        }
    }

    // Checks the selected printer to see if it has the pdf virtual device installed.
    private boolean isPDFEnabled(Connection connection) {
        try {
            String printerInfo = SGD.GET("apl.enable", connection);
            if (printerInfo.equals("pdf")) {
                return true;
            }
        } catch (ConnectionException e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean enablePDF(Connection connection) {

        try {
            SGD.SET("apl.enable", "pdf", connection);
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
        return true;
    }

    // Sets the scaling on the printer and then sends the pdf file to the printer
    private void sendPrint(String MacAddress) {

        Log.i(LOGDEBUG, "sendPrint()");
        //Connection connection = new BluetoothConnection(MacAddress);

        try {
            /*if(!this.isConnected()) {
                 connection.open();
                 ZebraPrinter printer = ZebraPrinterFactory.getInstance(connection);
            }*/
            //boolean isReady = checkPrinterStatus(printer);
            boolean isReady = true;
            try {
                String scale = scalePrint(printerConnection);
                SGD.SET("apl.settings", scale, printerConnection);
            } catch (ConnectionException e) {
                Log.d(LOGDEBUG, String.valueOf(e));
            }

            if (isReady) {
                if (filePath != null) {

                    printer.sendFileContents(filePath);
                    //printer.sendFileContents(filePath,updateProgress);

                } else {
                    Log.i(LOGDEBUG, "NO PDF SELECTED");
                }
            } else {
                //showPrinterStatus(printer);
            }

        } catch (ConnectionException e) {
            e.printStackTrace();
            Log.i(LOGDEBUG, "ConnectionException", e);

            // } finally {

            //     try {
            //         printerConnection.close();
            //     } catch (ConnectionException e) {
            //         e.printStackTrace();
            //         Log.i(LOGDEBUG,"ZebraPrinterLanguageUnknownException",e);
            //     }
        }
    }

    /*ProgressMonitor updateProgress = new ProgressMonitor() {
        @Override
        public void updateProgress(int bytesWritten, int totalBytes) {
            // Monitors Sending file to Printer
            //bridge.triggerDocumentJSEvent("ZebraProgressMonitor",  "{ 'written': '"+bytesWritten+"','total': '"+totalBytes+"' }");
            Log.i(LOGDEBUG,"progressMonitor "+bytesWritten+", "+totalBytes);
        }
    };*/

    // Uses the Uri to obtain the name of the pdf.
    private String getPDFName(Uri fileUri) {

        String fileString = fileUri.toString();
        File myFile = new File(fileString);
        String fileName = null;

        if (fileString.startsWith("content://")) {
            Cursor cursor = null;
            try {
                cursor = getActivity().getContentResolver().query(fileUri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        } else if (fileString.startsWith("file://")) {
            fileName = myFile.getName();
        }
        return fileName;
    }

    // Returns the width of the page in inches for scaling later
    // PdfRenderer is only available for devices running Android Lollipop or newer
    private Integer getPageWidth(Uri fileUri) throws IOException {
        final ParcelFileDescriptor pfdPdf = getActivity().getContentResolver().openFileDescriptor(
                fileUri, "r");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            PdfRenderer pdf = new PdfRenderer(pfdPdf);
            PdfRenderer.Page page = pdf.openPage(0);
            int pixWidth = page.getWidth();
            int inWidth = pixWidth / 72;
            return inWidth;
        }

        return 0;
    }

    /**
     * Print PDF: Takes the size of the pdf and the printer's maximum size and scales the file down
     *
     * @param connection
     * @return
     * @throws ConnectionException
     */
    private String scalePrint(Connection connection) throws ConnectionException {
        int fileWidth = this.fileWidth; // Larghezza file, nell'esempio fornito era = 0;
        String scale = "dither scale-to-fit";

        if (fileWidth != 0) {
            String printerModel = SGD.GET("device.host_identification", connection).substring(0, 5);
            double scaleFactor;

            if (printerModel.equals("iMZ22") || printerModel.equals("QLn22") || printerModel.equals("ZD410")) {
                scaleFactor = 2.0 / fileWidth * 100;
            } else if (printerModel.equals("iMZ32") || printerModel.equals("QLn32") || printerModel.equals("ZQ510")) {
                scaleFactor = 3.0 / fileWidth * 100;
            } else if (printerModel.equals("QLn42") || printerModel.equals("ZQ520") ||
                    printerModel.equals("ZD420") || printerModel.equals("ZD500") ||
                    printerModel.equals("ZT220") || printerModel.equals("ZT230") ||
                    printerModel.equals("ZT410")) {
                scaleFactor = 4.0 / fileWidth * 100;
            } else if (printerModel.equals("ZT420")) {
                scaleFactor = 6.5 / fileWidth * 100;
            } else {
                scaleFactor = 100;
            }

            scale = "dither scale=" + (int) scaleFactor + "x" + (int) scaleFactor;
        }

        return scale;
    }



}
