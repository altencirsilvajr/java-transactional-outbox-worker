import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'filterStatus', standalone: true, pure: true })
export class FilterStatusPipe implements PipeTransform {
  transform<T extends { status: string }>(values: T[], status: string): T[] {
    return values.filter((value) => value.status === status);
  }
}
