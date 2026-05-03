import { Routes } from '@angular/router';
import { AppLayout } from './components/layout/app.layout';
import { DashboardComponent } from './components/dashboard/dashboard';

export const routes: Routes = [
    {
        path: '',
        component: AppLayout,
        children: [
            { path: '', component: DashboardComponent }
        ]
    }, { path: '**', redirectTo: '' }
];
