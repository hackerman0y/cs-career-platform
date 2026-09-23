import { Component, OnInit, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink, RouterLinkActive } from '@angular/router';
import { forkJoin } from 'rxjs';
import { AuthStore } from '../../core/auth/auth.store';
import { StudentProfileStore } from '../../core/profile/student-profile.store';

const api = 'http://localhost:8080/api';
type Step = { id:string; title:string; description:string; orderIndex:number; completed:boolean };
type Project = { id:string; name:string; description:string; technologies:string; githubUrl:string; liveDemoUrl:string };
type Portfolio = { username:string; fullName:string; targetCareer:string; headline:string; bio:string; published:boolean; projects:Project[] };
type Guide = { slug:string; title:string; summary:string; actions?:string[] };

@Component({selector:'app-career-workspace',imports:[FormsModule,RouterLink,RouterLinkActive],templateUrl:'./career-workspace.component.html',styleUrl:'./career-workspace.component.css'})
export class CareerWorkspaceComponent implements OnInit {
  private http=inject(HttpClient); private route=inject(ActivatedRoute); private router=inject(Router); private auth=inject(AuthStore); private profileStore=inject(StudentProfileStore);
  view='overview'; profile=this.profileStore.profile; steps:Step[]=[]; checklist:Step[]=[]; projects:Project[]=[]; guides:Guide[]=[]; selectedGuide:Guide|null=null; portfolio:Portfolio|null=null;
  loading=true; error=''; saving=false; editingId='';
  projectForm={name:'',description:'',technologies:'',githubUrl:'',liveDemoUrl:''};
  ngOnInit(){this.route.data.subscribe(data=>{this.view=data['view'];this.load();});}
  load(){if(!this.auth.isAuthenticated()){void this.router.navigateByUrl('/login');return;}this.loading=true;this.error='';forkJoin({profile:this.profileStore.load(),steps:this.http.get<Step[]>(`${api}/roadmap`),checklist:this.http.get<Step[]>(`${api}/checklist`),projects:this.http.get<Project[]>(`${api}/projects`),portfolio:this.http.get<Portfolio>(`${api}/portfolio`),guides:this.http.get<Guide[]>(`${api}/guides/freelancing`)}).subscribe({next:r=>{this.steps=r.steps;this.checklist=r.checklist;this.projects=r.projects;this.portfolio=r.portfolio;this.guides=r.guides;this.loading=false;},error:()=>{this.error='Could not load your workspace. Make sure the backend is running, then try again.';this.loading=false;}});}
  get roadmapPercent(){return this.steps.length?Math.round(this.steps.filter(x=>x.completed).length/this.steps.length*100):0;}
  get checklistPercent(){return this.checklist.length?Math.round(this.checklist.filter(x=>x.completed).length/this.checklist.length*100):0;}
  get completedSteps(){return this.steps.filter(step=>step.completed).length;}
  get completedChecklist(){return this.checklist.filter(item=>item.completed).length;}
  get nextStep(){return this.steps.find(step=>!step.completed);}
  toggle(kind:'roadmap'|'checklist',item:Step){this.http.put<Step>(`${api}/${kind}/${item.id}`,{completed:!item.completed}).subscribe({next:x=>Object.assign(item,x),error:()=>this.error='Could not save this change.'});}
  saveProject(){if(!this.projectForm.name.trim()||!this.projectForm.description.trim()||!this.projectForm.technologies.trim())return;this.saving=true;const request=this.editingId?this.http.put<Project>(`${api}/projects/${this.editingId}`,this.projectForm):this.http.post<Project>(`${api}/projects`,this.projectForm);request.subscribe({next:()=>{this.resetProject();this.load();},error:()=>{this.error='Could not save the project.';this.saving=false;}});}
  editProject(p:Project){this.editingId=p.id;this.projectForm={name:p.name,description:p.description,technologies:p.technologies,githubUrl:p.githubUrl||'',liveDemoUrl:p.liveDemoUrl||''};}
  deleteProject(p:Project){if(!confirm(`Delete ${p.name}?`))return;this.http.delete(`${api}/projects/${p.id}`).subscribe({next:()=>this.load(),error:()=>this.error='Could not delete the project.'});}
  resetProject(){this.editingId='';this.projectForm={name:'',description:'',technologies:'',githubUrl:'',liveDemoUrl:''};this.saving=false;}
  savePortfolio(){if(!this.portfolio)return;this.saving=true;this.http.put<Portfolio>(`${api}/portfolio`,this.portfolio).subscribe({next:p=>{this.portfolio=p;this.saving=false;},error:()=>{this.error='Could not save the portfolio. Check that the username is unique.';this.saving=false;}});}
  openGuide(g:Guide){this.http.get<Guide>(`${api}/guides/freelancing/${g.slug}`).subscribe(x=>this.selectedGuide=x);}
  logout(){this.auth.logout();void this.router.navigateByUrl('/login');}
}
