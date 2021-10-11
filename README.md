# capacitor-zebra-pdfprint

provides a plugin to print with Zebra Printer portable printers

## Install

```bash
npm install capacitor-zebra-pdfprint
npx cap sync
```

## API

<docgen-index>

* [`echo(...)`](#echo)
* [`printZPL(...)`](#printzpl)
* [`printPDF(...)`](#printpdf)
* [`printerStatus(...)`](#printerstatus)
* [`searchDevices(...)`](#searchdevices)
* [`searchPrinters(...)`](#searchprinters)
* [`isConnected()`](#isconnected)
* [`connect(...)`](#connect)
* [`disconnect()`](#disconnect)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### echo(...)

```typescript
echo(options: { value: string; }) => any
```

Test di connessione al Plugin

| Param         | Type                            |
| ------------- | ------------------------------- |
| **`options`** | <code>{ value: string; }</code> |

**Returns:** <code>any</code>

--------------------


### printZPL(...)

```typescript
printZPL(options: { cpcl: string; }) => any
```

Stampa in formato ZPL

| Param         | Type                           |
| ------------- | ------------------------------ |
| **`options`** | <code>{ cpcl: string; }</code> |

**Returns:** <code>any</code>

--------------------


### printPDF(...)

```typescript
printPDF(options: { uri: string; }) => any
```

Stampa in formatp PDF

| Param         | Type                          |
| ------------- | ----------------------------- |
| **`options`** | <code>{ uri: string; }</code> |

**Returns:** <code>any</code>

--------------------


### printerStatus(...)

```typescript
printerStatus(options: { MACAddress: string; }) => any
```

Returns Printer Status with these properties (read-only):
(see <a href="#printerstatus">PrinterStatus</a> interface)

| Param         | Type                                 |
| ------------- | ------------------------------------ |
| **`options`** | <code>{ MACAddress: string; }</code> |

**Returns:** <code>any</code>

--------------------


### searchDevices(...)

```typescript
searchDevices(options: { printers: string; }) => any
```

| Param         | Type                               |
| ------------- | ---------------------------------- |
| **`options`** | <code>{ printers: string; }</code> |

**Returns:** <code>any</code>

--------------------


### searchPrinters(...)

```typescript
searchPrinters(options: { value: string; }) => any
```

| Param         | Type                            |
| ------------- | ------------------------------- |
| **`options`** | <code>{ value: string; }</code> |

**Returns:** <code>any</code>

--------------------


### isConnected()

```typescript
isConnected() => any
```

**Returns:** <code>any</code>

--------------------


### connect(...)

```typescript
connect(options: { MACAddress: string; }) => any
```

| Param         | Type                                 |
| ------------- | ------------------------------------ |
| **`options`** | <code>{ MACAddress: string; }</code> |

**Returns:** <code>any</code>

--------------------


### disconnect()

```typescript
disconnect() => void
```

--------------------


### Interfaces


#### PrinterStatus

| Prop                            | Type                 |
| ------------------------------- | -------------------- |
| **`connected`**                 | <code>boolean</code> |
| **`isReadyToPrint`**            | <code>boolean</code> |
| **`isPaused`**                  | <code>boolean</code> |
| **`isReceiveBufferFull`**       | <code>boolean</code> |
| **`isRibbonOut`**               | <code>boolean</code> |
| **`isPaperOut`**                | <code>boolean</code> |
| **`isHeadTooHot`**              | <code>boolean</code> |
| **`isHeadOpen`**                | <code>boolean</code> |
| **`isHeadCold`**                | <code>boolean</code> |
| **`isPartialFormatInProgress`** | <code>boolean</code> |
| **`isPDFEnabled`**              | <code>boolean</code> |
| **`friendlyName`**              | <code>string</code>  |
| **`macAddress`**                | <code>string</code>  |
| **`MAC_ADDRESS`**               | <code>string</code>  |
| **`PRODUCT_NAME`**              | <code>string</code>  |
| **`searching`**                 | <code>boolean</code> |

</docgen-api>
