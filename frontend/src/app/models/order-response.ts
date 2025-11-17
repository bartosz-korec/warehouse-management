export interface OrderResponse {
  warehouses: GridDto[];
  locations: OrderLocationDto[];
}

export interface GridDto {
  id: number;
  layout: number[][];
}

export interface OrderLocationDto {
  label: string;
  point: PointDto;
}

export interface PointDto {
  gridIndex: number;
  x: number;
  y: number;
}
