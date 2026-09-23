import { Routes } from '@angular/router';
import { ProfilePageComponent } from './features/profile/profile-page.component';
import { AuthPageComponent } from './features/auth/auth-page.component';
import { AssessmentPageComponent } from './features/assessment/assessment-page.component';
import { CareerWorkspaceComponent } from './features/workspace/career-workspace.component';
import { PublicPortfolioComponent } from './features/portfolio/public-portfolio.component';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
  { path: 'dashboard', component: CareerWorkspaceComponent, data: { view: 'overview' } },
  { path: 'profile', component: ProfilePageComponent },
  { path: 'login', component: AuthPageComponent, data: { mode: 'login' } },
  { path: 'register', component: AuthPageComponent, data: { mode: 'register' } },
  { path: 'assessment', component: AssessmentPageComponent },
  { path: 'roadmap', component: CareerWorkspaceComponent, data: { view: 'roadmap' } },
  { path: 'projects', component: CareerWorkspaceComponent, data: { view: 'projects' } },
  { path: 'portfolio', component: CareerWorkspaceComponent, data: { view: 'portfolio' } },
  { path: 'checklist', component: CareerWorkspaceComponent, data: { view: 'checklist' } },
  { path: 'freelancing', component: CareerWorkspaceComponent, data: { view: 'freelancing' } },
  { path: 'p/:username', component: PublicPortfolioComponent },
  { path: '**', redirectTo: 'dashboard' }
];
