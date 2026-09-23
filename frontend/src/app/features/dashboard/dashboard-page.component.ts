import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { StudentProfileStore } from '../../core/profile/student-profile.store';

type RoadmapStep = {
  title: string;
  description: string;
  status: 'complete' | 'current' | 'upcoming';
};

@Component({
  selector: 'app-dashboard-page',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './dashboard-page.component.html',
  styleUrl: './dashboard-page.component.css'
})
export class DashboardPageComponent {
  private readonly profileStore = inject(StudentProfileStore);

  readonly profile = this.profileStore.profile;
  readonly progress = 38;
  readonly roadmapSteps: RoadmapStep[] = [
    { title: 'HTML & CSS foundations', description: 'Structure, styles, and responsive layouts', status: 'complete' },
    { title: 'JavaScript fundamentals', description: 'Events, DOM updates, and browser storage', status: 'current' },
    { title: 'Build a portfolio project', description: 'Turn the basics into something you can share', status: 'upcoming' }
  ];
}
