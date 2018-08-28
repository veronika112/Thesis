import {Component, EventEmitter, Output} from '@angular/core';

@Component({
  selector: 'app-root',
  template: `
    <app-subjects-list [semesterId]="semId"></app-subjects-list>
<app-subjects-list [groups]="gr"></app-subjects-list>
  `,
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'app';

  semId = null;
  gr = null;

  submit(semesterId, groups) {
    console.log("button submit clicked: " + semesterId + ", " + groups);

    this.semId = semesterId;
    this.gr = groups;
  }

}
