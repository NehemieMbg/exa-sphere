'use server';

import { noAuthFetch } from '@/utils/apis/exaSphereApi';
import { AxiosError } from 'axios';
import getUserToken from '../user/getUserToken';

interface IProfileAction {
  id?: number;
  title: string;
  description?: string | undefined;
  skills: string;
  experience1: {
    jobTitle: string;
    company: string;
    location: string;
    startDate: Date;
    endDate?: Date | undefined;
    responsibilities: string;
    achievements?: string | undefined;
  };
  experience2?:
    | {
        jobTitle?: string;
        company?: string;
        location?: string;
        startDate?: Date;
        endDate?: Date;
        responsibilities?: string;
        achievements?: string | undefined;
      }
    | undefined;
  experience3?:
    | {
        jobTitle?: string;
        company?: string;
        location?: string;
        startDate?: Date;
        endDate?: Date;
        responsibilities?: string;
        achievements?: string | undefined;
      }
    | undefined;
  characteristics: string;
}

const updateProfileAction = async (
  profileId: number,
  values: IProfileAction
) => {
  const accessToken = await getUserToken();

  values = { ...values, id: profileId };

  try {
    const response = await noAuthFetch.put(`/profiles`, values, {
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
    });

    console.log(response.data);
    return null;
  } catch (error: AxiosError | any) {
    const message = error.response.data.message;
    console.error(message);
    return { error: message };
  }
};

export default updateProfileAction;