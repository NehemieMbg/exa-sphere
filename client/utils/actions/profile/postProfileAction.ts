'use server';

import { noAuthFetch } from '@/utils/apis/exaSphereApi';
import { AxiosError } from 'axios';
import getUserToken from '../user/getUserToken';

interface Experience {
  jobTitle: string;
  company: string;
  location: string;
  startDate: string;
  endDate?: string | undefined;
  responsibilities: string;
  achievements?: string | undefined;
}

interface IProfileAction {
  title: string;
  description?: string | undefined;
  skills: string;
  experiences: Experience[];
  characteristics: string;
}

const postProfileAction = async (values: IProfileAction) => {
  const accessToken = await getUserToken();

  try {
    const response = await noAuthFetch.post(`/profiles`, values, {
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
    });

    return response.data;
  } catch (error: AxiosError | any) {
    const message = error.response.data.message;
    console.error(message);
    return { error: message };
  }
};

export default postProfileAction;
