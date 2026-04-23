import { Routes } from '@angular/router';
import { AppLayout } from './layout/component/app.layout';
import { DashboardComponent } from './dashboard';

export const routes: Routes = [
    {
        path: '',
        component: AppLayout,
        children: [
            { path: '', component: DashboardComponent }
        ]
    }, { path: '**', redirectTo: '' }
];
