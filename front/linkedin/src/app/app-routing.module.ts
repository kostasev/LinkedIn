import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { HomeComponent } from './home/home.component';
import {RegisterComponent} from './register/register.component';
import {AuthGuard} from './auth.guard';
import {SettingsComponent} from './settings/settings.component';
import {ProfileComponent} from './profile/profile.component';
import {NetworkComponent} from './network/network.component';
import {MessagesComponent} from './messages/messages.component';
import {JobsComponent} from './jobs/jobs.component';
import {NotificationsComponent} from './notifications/notifications.component';


const routes: Routes = [
  {
    path: 'home',
    redirectTo: '',
    pathMatch: 'full'
  },
  {
    path: '',
    component: HomeComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'register',
    component: RegisterComponent
  },
  {
    path: 'jobs',
    component: JobsComponent
  },
  {
    path: 'notifications',
    component: NotificationsComponent
  },
  {
    path: 'settings',
    component: SettingsComponent
  },
  {
    path: 'messages/:id',
    component: MessagesComponent
  },
  {
    path: 'profile/:id',
    component: ProfileComponent
  },
  {
    path: 'network',
    component: NetworkComponent
  },
  {
    path: '**',
    redirectTo: ''
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
