'use server';

import { noAuthFetch } from '@/utils/apis/exaSphereApi';
import getUserToken from '../user/getUserToken';
import { AxiosError } from 'axios';
import { IEducationAction } from '@/types';

const postEducationAction = async (values: IEducationAction[]) => {
  const accessToken = await getUserToken();

  try {
    const response = await noAuthFetch.post(
      `/educations`,
      { educations: values },
      {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      }
    );

    return response.data;
  } catch (error: AxiosError | any) {
    const message = error.response.data.message;
    console.error(message);
    return { error: message };
  }
};

export default postEducationAction;
