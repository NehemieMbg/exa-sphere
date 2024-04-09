'use client';

import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';

import { Repeat2, Keyboard, LogOut, Settings, Users } from 'lucide-react';

import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuGroup,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuShortcut,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import Link from 'next/link';
import toInitials from '@/utils/functions/toInitials';
import { useRouter } from 'next/navigation';

const NavMenu = () => {
  const router = useRouter();
  const email = '6naomi.liu@test.com';
  const fullName = 'Naomi Liu';
  const initials = toInitials(fullName);

  const handleLogout = () => {
    //? Implement logout logic here

    router.push('/sign-in');
  };

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Avatar className="cursor-pointer">
          <AvatarImage src="/avatar.png" alt="@shadcn" />
          <AvatarFallback>{initials}</AvatarFallback>
        </Avatar>
      </DropdownMenuTrigger>

      <DropdownMenuContent className="w-56 mx-6">
        <DropdownMenuLabel className="text-sm font-light">
          {email}
        </DropdownMenuLabel>

        <DropdownMenuSeparator />

        <DropdownMenuGroup className="px-0">
          <Link href="/my-profiles">
            <DropdownMenuItem>
              <Users className="mr-2 h-4 w-4" />
              <span>Profiles</span>
            </DropdownMenuItem>
          </Link>

          <Link href="/generate">
            <DropdownMenuItem>
              <Repeat2 className="mr-2 h-4 w-4" />
              <span>Generate</span>
            </DropdownMenuItem>
          </Link>

          <Link href="/settings">
            <DropdownMenuItem>
              <Settings className="mr-2 h-4 w-4" />
              <span>Settings</span>
            </DropdownMenuItem>
          </Link>
        </DropdownMenuGroup>

        <DropdownMenuSeparator />
        <DropdownMenuItem onClick={handleLogout}>
          <LogOut className="mr-2 h-4 w-4" />
          <span>Log out</span>
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
};
export default NavMenu;
