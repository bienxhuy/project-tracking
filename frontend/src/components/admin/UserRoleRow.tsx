type UserRoleRowProps = {
  label: string;
  value: number;
};

export function UserRoleRow({ label, value }: UserRoleRowProps) {
  return (
    <div className="flex items-center justify-between">
      <span className="text-gray-600">{label}</span>
      <span className="font-semibold text-gray-900">{value}</span>
    </div>
  );
}

