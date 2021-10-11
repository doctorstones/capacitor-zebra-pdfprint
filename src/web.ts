import { WebPlugin } from '@capacitor/core';

import type { ZebraPdfPrintPlugin, PrinterStatus } from './definitions';

export class ZebraPdfPrintWeb extends WebPlugin implements ZebraPdfPrintPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
  printZPL(options: { cpcl: string }):any { console.log(options); throw new Error('not implemented'); }
  printPDF(options: { uri: string }):any { console.log(options); throw new Error('not implemented'); }
  printerStatus(options: { MACAddress: string }):Promise<PrinterStatus> { console.log(options); throw new Error('not implemented'); }

  searchDevices(options: { printers: string }):any { console.log(options); throw new Error('not implemented'); }
  searchPrinters(options: { value: string }):any { console.log(options); throw new Error('not implemented'); }
  
  isConnected():any { throw new Error('not implemented'); }
  connect(options: { MACAddress: string }):any { console.log(options); throw new Error('not implemented'); }
  disconnect():any { throw new Error('not implemented'); }
}
