package com.infoservicenet.plugins.zebrapdf;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.database.Cursor;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.util.Log;

import com.getcapacitor.CapConfig;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;

import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;

import com.zebra.sdk.printer.PrinterLanguage;
import com.zebra.sdk.printer.PrinterStatus;
import com.zebra.sdk.printer.SGD;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;
import com.zebra.sdk.printer.discovery.BluetoothDiscoverer;
import com.zebra.sdk.printer.discovery.DiscoveredPrinter;
import com.zebra.sdk.printer.discovery.DiscoveredPrinterBluetooth;
import com.zebra.sdk.printer.discovery.DiscoveryHandler;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author DANIELE MASSI
 * @version 0.0.2;
 * @since 2021 10 11
 */
public class ZebraPdfPrint {

    public String echo(String value) {
        return value;
    }
}
