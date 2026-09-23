import { Component, OnInit, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CareerPath, StudentProfile } from '../../core/profile/student-profile.model';
import { StudentProfileStore } from '../../core/profile/student-profile.store';
const apiUrl = 'http://localhost:8080/api';
type Skill = { id:string; name:string; level:'beginner'|'intermediate'|'advanced'; source:string };

@Component({
  selector: 'app-profile-page',
  imports: [FormsModule, ReactiveFormsModule, RouterLink],
  templateUrl: './profile-page.component.html',
  styleUrl: './profile-page.component.css'
})
export class ProfilePageComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  readonly profileStore = inject(StudentProfileStore);
  private readonly router = inject(Router);
  private readonly http = inject(HttpClient);
  skills: Skill[] = [];
  skillName = '';
  skillLevel: Skill['level'] = 'beginner';
  skillsError = '';
  showSkillSuggestions = false;
  cvUploading = false;
  cvMessage = '';
  cvText = '';
  cvTextSaving = false;

  readonly careerPaths: { value: CareerPath; description: string }[] = [
    { value: 'Frontend Developer', description: 'Build clear, responsive experiences for the web.' },
    { value: 'Backend Developer', description: 'Design APIs, databases, and the systems behind products.' },
    { value: 'Mobile Developer', description: 'Create useful experiences for Android and iOS.' },
    { value: 'Data Analyst', description: 'Use data to find patterns and support better decisions.' },
    { value: 'AI / Machine Learning', description: 'Build intelligent products with data, models, and evaluation.' },
    { value: 'Cybersecurity', description: 'Protect systems, networks, and users through practical security work.' },
    { value: 'DevOps / Cloud', description: 'Automate delivery and build dependable cloud infrastructure.' },
    { value: 'Full Stack Developer', description: 'Build complete products from interface to deployment.' }
  ];

  readonly profileForm = this.formBuilder.nonNullable.group({
    fullName: [this.profileStore.profile().fullName, [Validators.required, Validators.maxLength(80)]],
    university: [this.profileStore.profile().university, Validators.maxLength(120)],
    academicYear: [this.profileStore.profile().academicYear, Validators.required],
    githubUrl: [this.profileStore.profile().githubUrl],
    linkedinUrl: [this.profileStore.profile().linkedinUrl],
    targetCareer: [this.profileStore.profile().targetCareer, Validators.required]
  });

  saveError = '';

  ngOnInit(): void {
    this.profileStore.load().subscribe({
      next: (profile) => this.profileForm.patchValue(profile)
    });
    this.loadSkills();
    this.http.get<{text:string}>(`${apiUrl}/profile/cv-text`).subscribe({next:result=>this.cvText=result.text||''});
  }

  loadSkills(): void { this.http.get<Skill[]>(`${apiUrl}/skills`).subscribe({ next: skills => this.skills = skills, error: () => this.skillsError = 'Could not load your skills.' }); }
  get suggestedSkills(): string[] { const career=this.profileForm.controls.targetCareer.value; const byCareer:Record<string,string[]>={ 'Frontend Developer':['HTML','CSS','JavaScript','TypeScript','Angular','React','Accessibility','Git'], 'Backend Developer':['Java','Spring Boot','SQL','PostgreSQL','REST APIs','JWT','Docker','Git'], 'Mobile Developer':['Kotlin','Java','Flutter','Dart','Android','REST APIs','Git'], 'Data Analyst':['Excel','SQL','Python','Pandas','Power BI','Tableau','Statistics','Data Visualization'], 'AI / Machine Learning':['Python','Pandas','NumPy','Scikit-learn','Machine Learning','Data Visualization','SQL','Docker'], 'Cybersecurity':['Linux','Networking','Python','Wireshark','OWASP','Web Security','Git'], 'DevOps / Cloud':['Linux','Git','Docker','Kubernetes','CI/CD','AWS','Terraform','Cloud'], 'Full Stack Developer':['HTML','CSS','JavaScript','TypeScript','Angular','Java','Spring Boot','SQL','REST APIs','Git','Docker'] }; const query=this.skillName.trim().toLowerCase(); const existing=new Set(this.skills.map(skill=>skill.name.toLowerCase())); return (byCareer[career]||[]).filter(skill=>!existing.has(skill.toLowerCase())&&(!query||skill.toLowerCase().includes(query))).slice(0,6); }
  selectSuggestion(name:string): void { this.skillName=name; this.showSkillSuggestions=false; }
  addSkill(): void { const name=this.skillName.trim(); if(!name) return; this.skillsError=''; this.http.post<Skill>(`${apiUrl}/skills`,{name,level:this.skillLevel}).subscribe({next:()=>{this.skillName='';this.showSkillSuggestions=false;this.loadSkills();},error:()=>this.skillsError='Could not save this skill.'}); }
  deleteSkill(skill: Skill): void { this.http.delete(`${apiUrl}/skills/${skill.id}`).subscribe({next:()=>this.loadSkills(),error:()=>this.skillsError='Could not remove this skill.'}); }
  uploadCv(event: Event): void { const input=event.target as HTMLInputElement; const file=input.files?.[0]; if(!file) return; this.cvUploading=true; this.cvMessage=''; const body=new FormData(); body.append('file',file); this.http.post<StudentProfile>(`${apiUrl}/profile/cv`,body).subscribe({next:profile=>{this.profileStore.update(profile).subscribe(); this.cvUploading=false;this.cvMessage='CV saved as evidence for your applications.';},error:error=>{this.cvUploading=false;this.cvMessage=error.error?.message||'Could not upload your CV. Use a PDF smaller than 5 MB.';}}); }
  saveCvText(): void { this.cvTextSaving=true; this.cvMessage=''; this.http.put<{text:string}>(`${apiUrl}/profile/cv-text`,{text:this.cvText}).subscribe({next:()=>{this.cvTextSaving=false;this.cvMessage='CV text saved. It will now be compared during Check fit.';},error:()=>{this.cvTextSaving=false;this.cvMessage='Could not save CV text.';}}); }

  saveProfile(): void {
    if (this.profileForm.invalid) {
      this.profileForm.markAllAsTouched();
      return;
    }

    this.saveError = '';
    this.profileStore.update(this.profileForm.getRawValue() as StudentProfile).subscribe({
      next: () => void this.router.navigateByUrl('/dashboard'),
      error: () => this.saveError = 'We could not save your profile. Please try again.'
    });
  }
}
