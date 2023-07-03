import { Component, Input } from '@angular/core';

/**
 * @internal
 */
@Component({
  selector: 'ov-avatar-profile',
  template: `
    <ng-container *ngIf="!isOnHold; else holdMessage">
      <div class="poster" id="video-poster">
        <div class="initial" [ngStyle]="{ 'background-color': color }">
          <span id="poster-text">{{ letter }}</span>
        </div>
      </div>
    </ng-container>
    <ng-template #holdMessage>
      <div class="poster on-hold-poster">
        <div class="initial" [ngStyle]="{ 'background-color': color }">
          <span id="poster-text">On Hold</span>
        </div>
      </div>
    </ng-template>
  `,
  styleUrls: ['./avatar-profile.component.css']
})
export class AvatarProfileComponent {
  letter: string;

  @Input()
  set name(nickname: string) {
    this.letter = nickname[0];
  }

  @Input() color;
  @Input() isOnHold: boolean;

  constructor() { }
}
