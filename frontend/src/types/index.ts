export interface GachaResponse<T> {
  success: boolean;
  message: string;
  data?: T;
}

export type GachaStatus = 'IDLE' | 'LOADING' | 'SUCCESS' | 'ERROR';
