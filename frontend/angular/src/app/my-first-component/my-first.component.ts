import { Component } from '@angular/core';
import {FormsModule} from "@angular/forms";
import {NgForOf, NgIf} from "@angular/common";

@Component({
  selector: 'app-my-first-component',
  standalone: true,
  imports: [
    FormsModule,
    NgIf,
    NgForOf
  ],
  templateUrl: './my-first.component.html',
  styleUrl: './my-first.component.scss'
})
export class MyFirstComponent {

  inputValue: string = `hello`;
  displayMsg = false;

  msgList:Array<string> = [];
  msgListComposed: any[] = [];

  clickMe(): void {
    this.msgList.push(this.inputValue);
    this.msgListComposed.push({
      name: this.inputValue,
      visible: true
    });
    this.inputValue='';
  }
}
