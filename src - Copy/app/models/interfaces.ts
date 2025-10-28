export interface NewJoiner {
  id?: number;
  name: string;
  empId: string;
  doj: string;
  location: string;
  techStack: string;
  resumePath?: string;
}

export interface AMSDetails {
  id?: number;
  amsName: string;
  managerName: string;
  location: string;
  contactDetails: string;
}

export interface UdemyCourse {
  id?: number;
  name: string;
  percentage: number;
  color?: string;
}

export interface Interview {
  id?: number;
  client: string;
  date: string;
  feedback: string;
}