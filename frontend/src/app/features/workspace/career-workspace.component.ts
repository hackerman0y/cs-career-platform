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
type GithubRepository = { name:string; description:string; githubUrl:string; homepage:string; language:string; topics:string[]; imported:boolean };
type GithubApiRepository = { name:string; description:string|null; html_url:string; homepage:string|null; language:string|null; topics:string[]; fork:boolean; archived:boolean };
type Portfolio = { username:string; fullName:string; targetCareer:string; headline:string; bio:string; published:boolean; projects:Project[] };
type Guide = { slug:string; title:string; summary:string; actions?:string[] };
type Resource = { id:string; title:string; provider:string; resourceType:string; url:string; language:string; costType:string; difficulty:string; estimatedDuration:string; description:string; verified:boolean };
type Readiness = { score:number; status:string; targetCareer:string; nextActions:{key:string;title:string;action:string;points:number}[]; evidence:{key:string;title:string;detail:string;source:string}[] };
type Opportunity = { id:string; title:string; company:string; opportunityType:'job'|'internship'; url:string; description:string; requirements:string; createdAt:string };
type DiscoveryLink = { title:string; description:string; provider:string; opportunityType:'job'|'internship'; url:string };
type Fit = { score:number; decision:string; matchedRequirements:string[]; missingRequirements:string[]; nextAction:string };
type Application = { id:string; opportunityId:string; opportunityTitle:string; company:string; status:string; appliedAt:string|null; followUpDate:string|null; notes:string; outcome:string };

@Component({selector:'app-career-workspace',imports:[FormsModule,RouterLink,RouterLinkActive],templateUrl:'./career-workspace.component.html',styleUrl:'./career-workspace.component.css'})
export class CareerWorkspaceComponent implements OnInit {
  private http=inject(HttpClient); private route=inject(ActivatedRoute); private router=inject(Router); private auth=inject(AuthStore); private profileStore=inject(StudentProfileStore);
  view='overview'; profile=this.profileStore.profile; steps:Step[]=[]; checklist:Step[]=[]; projects:Project[]=[]; guides:Guide[]=[]; selectedGuide:Guide|null=null; portfolio:Portfolio|null=null;
  readiness:Readiness|null=null; opportunities:Opportunity[]=[]; discoveryLinks:DiscoveryLink[]=[]; applications:Application[]=[]; resourceMap:Record<string,Resource[]>={}; expandedResources=''; selectedFit:Fit|null=null; githubRepos:GithubRepository[]=[]; githubSelection:Record<string,boolean>={}; githubLoading=false; githubImportMessage='';
  loading=true; error=''; applicationError=''; saving=false; editingId='';
  projectForm={name:'',description:'',technologies:'',githubUrl:'',liveDemoUrl:''};
  opportunityForm={title:'',company:'',opportunityType:'internship',url:'',description:'',requirements:''};
  applicationForm={opportunityId:'',status:'saved',appliedAt:'',followUpDate:'',notes:'',outcome:''};
  ngOnInit(){this.route.data.subscribe(data=>{this.view=data['view'];this.load();});}
  load(){if(!this.auth.isAuthenticated()){void this.router.navigateByUrl('/login');return;}this.loading=true;this.error='';forkJoin({profile:this.profileStore.load(),steps:this.http.get<Step[]>(`${api}/roadmap`),checklist:this.http.get<Step[]>(`${api}/checklist`),projects:this.http.get<Project[]>(`${api}/projects`),portfolio:this.http.get<Portfolio>(`${api}/portfolio`),guides:this.http.get<Guide[]>(`${api}/guides/freelancing`),readiness:this.http.get<Readiness>(`${api}/readiness`),opportunities:this.http.get<Opportunity[]>(`${api}/opportunities`),discovery:this.http.get<DiscoveryLink[]>(`${api}/opportunities/discover`),applications:this.http.get<Application[]>(`${api}/applications`)}).subscribe({next:r=>{this.steps=r.steps;this.checklist=r.checklist;this.projects=r.projects;this.portfolio=r.portfolio;this.guides=r.guides;this.readiness=r.readiness;this.opportunities=r.opportunities;this.discoveryLinks=r.discovery;this.applications=r.applications;this.loading=false;},error:()=>{this.error='Could not load your workspace. Make sure the backend is running, then try again.';this.loading=false;}});}
  get roadmapPercent(){return this.steps.length?Math.round(this.steps.filter(x=>x.completed).length/this.steps.length*100):0;}
  get checklistPercent(){return this.checklist.length?Math.round(this.checklist.filter(x=>x.completed).length/this.checklist.length*100):0;}
  get completedSteps(){return this.steps.filter(step=>step.completed).length;}
  get completedChecklist(){return this.checklist.filter(item=>item.completed).length;}
  get nextStep(){return this.steps.find(step=>!step.completed);}
  get actionToday(){return this.readiness?.nextActions[0];}
  toggle(kind:'roadmap'|'checklist',item:Step){this.http.put<Step>(`${api}/${kind}/${item.id}`,{completed:!item.completed}).subscribe({next:x=>{Object.assign(item,x);this.refreshReadiness();},error:()=>this.error='Could not save this change.'});}
  refreshReadiness(){this.http.post<Readiness>(`${api}/readiness/recalculate`,{}).subscribe({next:r=>this.readiness=r});}
  toggleResources(step:Step){if(this.expandedResources===step.id){this.expandedResources='';return;}this.expandedResources=step.id;if(!this.resourceMap[step.id])this.http.get<Resource[]>(`${api}/roadmap/${step.id}/resources`).subscribe({next:r=>this.resourceMap[step.id]=r,error:()=>this.error='Could not load learning resources.'});}
  saveProject(){if(!this.projectForm.name.trim()||!this.projectForm.description.trim()||!this.projectForm.technologies.trim())return;this.saving=true;const request=this.editingId?this.http.put<Project>(`${api}/projects/${this.editingId}`,this.projectForm):this.http.post<Project>(`${api}/projects`,this.projectForm);request.subscribe({next:()=>{this.resetProject();this.load();},error:()=>{this.error='Could not save the project.';this.saving=false;}});}
  editProject(p:Project){this.editingId=p.id;this.projectForm={name:p.name,description:p.description,technologies:p.technologies,githubUrl:p.githubUrl||'',liveDemoUrl:p.liveDemoUrl||''};}
  deleteProject(p:Project){if(!confirm(`Delete ${p.name}?`))return;this.http.delete(`${api}/projects/${p.id}`).subscribe({next:()=>this.load(),error:()=>this.error='Could not delete the project.'});}
  resetProject(){this.editingId='';this.projectForm={name:'',description:'',technologies:'',githubUrl:'',liveDemoUrl:''};this.saving=false;}
  loadGithubPreview(){this.githubLoading=true;this.githubImportMessage='';this.http.get<GithubRepository[]>(`${api}/profile/github-repositories`).subscribe({next:repos=>{this.githubRepos=repos;this.githubSelection={};repos.forEach(repo=>this.githubSelection[repo.githubUrl]=!repo.imported);this.githubLoading=false;},error:error=>{this.githubLoading=false;this.githubImportMessage=error.error?.message || 'Could not read public GitHub repositories. Check your GitHub profile URL and try again.';}});}
  importGithubProjects(){const repositories=this.githubRepos.filter(repo=>this.githubSelection[repo.githubUrl]&&!repo.imported).map(({imported,...repository})=>repository);if(!repositories.length){this.githubImportMessage='Select at least one repository that has not already been imported.';return;}this.githubLoading=true;this.http.post<{importedCount:number}>(`${api}/profile/import-github`,{repositories}).subscribe({next:result=>{this.githubImportMessage=`Imported ${result.importedCount} project${result.importedCount===1?'':'s'} from GitHub. They are now included in your portfolio.`;this.githubLoading=false;this.load();this.githubRepos=[];},error:error=>{this.githubLoading=false;this.githubImportMessage=error.error?.message || 'Could not import the selected repositories.';}});}
  private githubUsername(url:string){try{const parsed=new URL(url);if((parsed.hostname==='github.com'||parsed.hostname==='www.github.com')&&parsed.pathname.split('/').filter(Boolean).length===1)return parsed.pathname.split('/').filter(Boolean)[0];}catch{}return '';}
  savePortfolio(){if(!this.portfolio)return;this.saving=true;this.http.put<Portfolio>(`${api}/portfolio`,this.portfolio).subscribe({next:p=>{this.portfolio=p;this.saving=false;this.refreshReadiness();},error:()=>{this.error='Could not save the portfolio. Check that the username is unique.';this.saving=false;}});}
  saveOpportunity(){if(!this.opportunityForm.title.trim()||!this.opportunityForm.company.trim()||!this.opportunityForm.description.trim()||!this.opportunityForm.requirements.trim())return;this.saving=true;this.http.post<Opportunity>(`${api}/opportunities`,this.opportunityForm).subscribe({next:()=>{this.opportunityForm={title:'',company:'',opportunityType:'internship',url:'',description:'',requirements:''};this.saving=false;this.load();},error:()=>{this.error='Could not save this opportunity.';this.saving=false;}});}
  deleteOpportunity(opportunity:Opportunity){if(!confirm(`Delete ${opportunity.title}?`))return;this.http.delete(`${api}/opportunities/${opportunity.id}`).subscribe({next:()=>this.load(),error:()=>this.error='Could not delete this opportunity.'});}
  checkFit(opportunity:Opportunity){this.http.get<Fit>(`${api}/opportunities/${opportunity.id}/fit`).subscribe({next:r=>this.selectedFit=r,error:()=>this.error='Could not evaluate this opportunity.'});}
  startApplication(opportunity:Opportunity){this.applicationError='';this.applicationForm={opportunityId:opportunity.id,status:'saved',appliedAt:'',followUpDate:'',notes:'',outcome:''};this.view='applications';}
  saveApplication(){if(!this.applicationForm.opportunityId){this.applicationError='Save an opportunity first, then select it here to track your application.';return;}this.applicationError='';this.saving=true;const body={...this.applicationForm,appliedAt:this.applicationForm.appliedAt||null,followUpDate:this.applicationForm.followUpDate||null};this.http.post<Application>(`${api}/applications`,body).subscribe({next:()=>{this.applicationForm={opportunityId:'',status:'saved',appliedAt:'',followUpDate:'',notes:'',outcome:''};this.saving=false;this.load();},error:()=>{this.applicationError='Could not save the application. Please try again.';this.saving=false;}});}
  updateApplicationStatus(application:Application,status:string){this.http.put<Application>(`${api}/applications/${application.id}`,{opportunityId:application.opportunityId,status,appliedAt:application.appliedAt,followUpDate:application.followUpDate,notes:application.notes,outcome:application.outcome}).subscribe({next:()=>this.load(),error:()=>this.error='Could not update the application.'});}
  deleteApplication(application:Application){if(!confirm(`Delete application for ${application.opportunityTitle}?`))return;this.http.delete(`${api}/applications/${application.id}`).subscribe({next:()=>this.load(),error:()=>this.error='Could not delete the application.'});}
  openGuide(g:Guide){this.http.get<Guide>(`${api}/guides/freelancing/${g.slug}`).subscribe(x=>this.selectedGuide=x);}
  logout(){this.auth.logout();void this.router.navigateByUrl('/login');}
}
