'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { z } from 'zod';

import { Button } from '@/components/ui/button';
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form';
import { Textarea } from '@/components/ui/textarea';
import { cn } from '@/lib/utils';
import { IUser } from '@/types';
import { Loader2 } from 'lucide-react';
import { useState } from 'react';

const formSchema = z.object({
  description: z.string().min(1).max(250).optional(),
});

const UpdateDescriptionForm = ({ user }: { user: IUser }) => {
  const [isLoading, setIsLoading] = useState(false);

  // 1. Define your form.
  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      description: user.description,
    },
  });

  // 2. Define a submit handler.
  function onSubmit(values: z.infer<typeof formSchema>) {
    // Do something with the form values.
    // ✅ This will be type-safe and validated.
    console.log(values);
  }

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
        <FormField
          control={form.control}
          name="description"
          render={({ field }) => (
            <FormItem>
              <FormLabel className="font-light">Description</FormLabel>
              <FormControl>
                <Textarea placeholder="Type your message here." {...field} />
              </FormControl>
              <FormDescription>
                A short description about yourself. Max 250 characters.
              </FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />

        <Button
          type="submit"
          disabled={isLoading}
          className="bg-white hover:bg-gray-1 text-gray-3 h-9"
        >
          <Loader2
            className={cn('mr-2 h-4 w-4 animate-spin', {
              hidden: !isLoading,
            })}
          />
          <span>{isLoading ? 'updating...' : 'Submit'}</span>
        </Button>
      </form>
    </Form>
  );
};
export default UpdateDescriptionForm;
