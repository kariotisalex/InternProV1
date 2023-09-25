import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'app-footer-paging',
  templateUrl: './footer-paging.component.html',
  styleUrls: ['./footer-paging.component.css']
})
export class FooterPagingComponent {

  @Input() pages : number[] = [];
  @Output() myData = new EventEmitter<number>();
  customPages : number[] = [];


  btnclick(page : number){
    this.myData.emit(page);
  }

}
