export type CareerPath = 'Frontend Developer' | 'Backend Developer' | 'Mobile Developer' | 'Data Analyst' | 'AI / Machine Learning' | 'Cybersecurity' | 'DevOps / Cloud' | 'Full Stack Developer';

export interface StudentProfile {
  fullName: string;
  university: string;
  academicYear: string;
  githubUrl: string;
  linkedinUrl: string;
  targetCareer: CareerPath;
  cvFileName: string;
}
