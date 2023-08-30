import { Component } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-testing',
  standalone:true,
  imports:[
    ReactiveFormsModule
  ],
  template: './testing.component.html',
  styleUrls: ['./testing.component.css']
})
export class TestingComponent {
  favoriteColorControl = new FormControl('');
}

