
export interface PrinterStatus {
  connected: boolean;
  //
  isReadyToPrint?: boolean;
  isPaused?: boolean;
  isReceiveBufferFull?: boolean;
  isRibbonOut?: boolean;
  isPaperOut?: boolean;
  isHeadTooHot?: boolean;
  isHeadOpen?: boolean;
  isHeadCold?: boolean;
  isPartialFormatInProgress?: boolean;
  isPDFEnabled?: boolean,
  //
  friendlyName?:string,
  macAddress?:string,
  MAC_ADDRESS?: string,
  PRODUCT_NAME?: string,
  searching?:boolean,
}

export interface ZebraPdfPrintPlugin {
  /**
   * Test di connessione al Plugin
   * @param options 
   */
  echo(options: { value: string }): Promise<{ value: string }>;
  /** Stampa in formato ZPL */
  printZPL(options: { cpcl: string }): Promise<any>;
  /** Stampa in formatp PDF */
  printPDF(options: { uri: string }): Promise<any>;
  /** Stato Printer */
  printerStatus(options: { MACAddress: string }): Promise<PrinterStatus>;

  searchDevices(options: { printers: string }): Promise<{ printers: string }>;
  searchPrinters(options: { value: string }): Promise<any>;
  
  isConnected(): Promise<{ connected: boolean }>;
  connect(options: { MACAddress: string }): Promise<{ success: boolean }>;
  disconnect(): void;

}
