import { Component } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-testing',
  standalone:true,
  imports:[
    ReactiveFormsModule
  ],
  template: `Favorite Color: <input type="text" [formControl]="favoriteColorControl">
  <p>{{favoriteColorControl.value}}</p>`,
  styleUrls: ['./testing.component.css']
})
export class TestingComponent {
  favoriteColorControl = new FormControl('');
}

