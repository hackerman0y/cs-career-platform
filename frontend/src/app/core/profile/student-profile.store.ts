import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { Observable, of, tap } from 'rxjs';
import { AuthStore } from '../auth/auth.store';
import { StudentProfile } from './student-profile.model';

const storageKey = 'careerpath.student-profile';
const apiUrl = 'http://localhost:8080/api';

const defaultProfile: StudentProfile = {
  fullName: 'Student',
  university: '',
  academicYear: 'Year 2',
  githubUrl: '',
  linkedinUrl: '',
  targetCareer: 'Frontend Developer',
  cvFileName: ''
};

@Injectable({ providedIn: 'root' })
export class StudentProfileStore {
  private readonly http = inject(HttpClient);
  private readonly authStore = inject(AuthStore);
  private readonly profileState = signal<StudentProfile>(this.readProfile());

  readonly profile = this.profileState.asReadonly();

  load(): Observable<StudentProfile> {
    if (!this.authStore.isAuthenticated()) {
      return of(this.profileState());
    }

    return this.http.get<StudentProfile>(`${apiUrl}/profile`).pipe(tap((profile) => this.saveLocally(profile)));
  }

  update(profile: StudentProfile): Observable<StudentProfile> {
    if (!this.authStore.isAuthenticated()) {
      this.saveLocally(profile);
      return of(profile);
    }

    const { cvFileName, ...request } = profile;
    return this.http.put<StudentProfile>(`${apiUrl}/profile`, request).pipe(tap((savedProfile) => this.saveLocally(savedProfile)));
  }

  private saveLocally(profile: StudentProfile): void {
    this.profileState.set(profile);
    localStorage.setItem(storageKey, JSON.stringify(profile));
  }

  private readProfile(): StudentProfile {
    const savedProfile = localStorage.getItem(storageKey);

    if (!savedProfile) {
      return defaultProfile;
    }

    try {
      return { ...defaultProfile, ...JSON.parse(savedProfile) as Partial<StudentProfile> };
    } catch {
      return defaultProfile;
    }
  }
}
