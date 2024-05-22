'use client';

import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';

import { LogOut, Repeat2, Settings, Users } from 'lucide-react';

import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuGroup,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import toInitials from '@/utils/functions/toInitials';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import logoutAction from '@/utils/actions/authentication/logoutAction';
import { useAppSelector } from '@/lib/hooks';

const NavMenu = () => {
  const router = useRouter();
  const user = useAppSelector((state) => state.user);

  const fullName = `${user.firstName} ${user.lastName}`;
  const initials = toInitials(fullName);

  const handleLogout = async () => {
    //? Implement logout logic here
    const response = await logoutAction();

    if (response && response.error) {
      //? Show a toast message

      console.error(response.error);
      return;
    }

    router.refresh();
  };

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Avatar className="cursor-pointer max-lg:size-8">
          <AvatarImage src={user.avatar} alt="@shadcn" />
          <AvatarFallback>{initials}</AvatarFallback>
        </Avatar>
      </DropdownMenuTrigger>

      <DropdownMenuContent className="w-56 mx-6 z-[120]">
        <DropdownMenuLabel className="text-sm font-light">
          {user.email}
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
