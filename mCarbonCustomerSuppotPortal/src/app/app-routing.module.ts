import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ConnectpageComponent } from './mycomp/connectpage/connectpage.component';
import { LoginpageComponent } from './mycomp/loginpage/loginpage.component';

const routes: Routes = [
  { path: '', component: LoginpageComponent },
  { path: 'connectpage', component: ConnectpageComponent },];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
