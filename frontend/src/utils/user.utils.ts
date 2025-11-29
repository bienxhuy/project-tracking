// Utility functions for user-related operations

/**
 * Extract initials from a full name
 * @param name - Full name of the user
 * @returns Initials (first letter of first name + first letter of last name)
 */
export const getInitials = (name: string): string => {
  if (!name) return "";
  
  const parts = name.trim().split(" ");
  
  if (parts.length >= 2) {
    // First letter of first name + first letter of last name
    return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
  }
  
  // If only one word, take first two characters
  return name.substring(0, 2).toUpperCase();
};
