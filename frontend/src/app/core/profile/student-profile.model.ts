export type CareerPath = 'Frontend Developer' | 'Backend Developer' | 'Mobile Developer' | 'Data Analyst';

export interface StudentProfile {
  fullName: string;
  university: string;
  academicYear: string;
  githubUrl: string;
  linkedinUrl: string;
  targetCareer: CareerPath;
}

