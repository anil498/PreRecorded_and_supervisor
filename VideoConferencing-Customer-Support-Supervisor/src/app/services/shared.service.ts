import { Injectable } from '@angular/core';
import { ReplaySubject, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SharedService {
  private Data = new Subject<string>();



  emitData(data: string) {
    this.Data.next(data);
  }

  getData() {
    return this.Data.asObservable();
  }

  

}





