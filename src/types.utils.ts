/** Make object types more readable. */
export type Prettify<T> = {
  [K in keyof T]: T[K];
} & unknown;

export type ObjectValues<T> = T[keyof T];
