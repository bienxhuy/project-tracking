import { clsx, type ClassValue } from "clsx";
import { twMerge } from "tailwind-merge";

// This function is used to merge class names conditionally and handle Tailwind CSS class conflicts.
export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}
