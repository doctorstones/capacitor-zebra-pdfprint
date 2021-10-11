import { registerPlugin } from '@capacitor/core';

import type { ZebraPdfPrintPlugin } from './definitions';

const ZebraPdfPrint = registerPlugin<ZebraPdfPrintPlugin>('ZebraPdfPrint', {
  web: () => import('./web').then(m => new m.ZebraPdfPrintWeb()),
});

export * from './definitions';
export { ZebraPdfPrint };
