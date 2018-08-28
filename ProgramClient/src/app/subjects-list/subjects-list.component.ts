import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-subjects-list',
  templateUrl: './subjects-list.component.html',
  styleUrls: ['./subjects-list.component.css']
})
export class SubjectsListComponent implements OnInit {

  @Input() semesterId: string;
  @Input() groups: string;
  
  constructor() { }

  ngOnInit() {
    
    
  }
  
  displayValues(semesterId, groups){
    console.log("aa " + semesterId);
  }

}
