import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {MyFirstComponent} from "./my-first-component/my-first.component";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, MyFirstComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'angular';
}
