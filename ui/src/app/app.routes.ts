import { Routes } from '@angular/router';
import { SearchPage } from './search-page/search-page';
import { AdminPage } from './admin-page/admin-page';

export const routes: Routes = [
  { path: '', component: SearchPage },
  { path: 'admin', component: AdminPage },
  { path: '**', redirectTo: '' }
];
