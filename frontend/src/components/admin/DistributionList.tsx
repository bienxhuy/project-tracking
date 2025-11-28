import type { DistributionEntry } from "@/utils/dashboard.utils";

type DistributionListProps = {
  data: DistributionEntry[];
  emptyMessage?: string;
};

export function DistributionList({
  data,
  emptyMessage = "No data available.",
}: DistributionListProps) {
  if (!data.length) {
    return <p className="text-sm text-gray-500">{emptyMessage}</p>;
  }

  const maxValue = Math.max(...data.map((item) => item.value));

  return (
    <div className="space-y-4">
      {data.map((item) => (
        <div key={item.label} className="flex items-center">
          <div className="flex-1">
            <p className="text-sm font-medium text-gray-800">{item.label}</p>
            <div className="mt-1 h-2 rounded-full bg-gray-100">
              <div
                className="h-2 rounded-full bg-indigo-500"
                style={{
                  width: maxValue ? `${(item.value / maxValue) * 100}%` : 0,
                }}
              />
            </div>
          </div>
          <span className="ml-4 text-sm font-semibold text-gray-900">
            {item.value || "No data available"}
          </span>
        </div>
      ))}
    </div>
  );
}

