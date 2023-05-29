import { Injectable } from '@angular/core';
import { ReplaySubject, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SharedService {
  private playPauseData = new Subject<string>();
  private muteData = new Subject<string>();
  private slideData = new Subject<string>();
  private ExitData = new Subject<string>();


  emitPlayPauseData(data: string) {
    this.playPauseData.next(data);
  }

  getPlayPauseData() {
    return this.playPauseData.asObservable();
  }

  emitMuteData(data: string) {
    this.muteData.next(data);
  }

  getMuteData() {
    return this.muteData.asObservable();
  }

  emitSlideData(data: string) {
    this.slideData.next(data);
  }

  getSlideData() {
    return this.slideData.asObservable();
  }

  emitExitData(data: string) {
    this.ExitData.next(data);
  }

  getExitData() {
    return this.ExitData.asObservable();
  }
  

}





