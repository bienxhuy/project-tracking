/* eslint-disable react/forbid-dom-props */

type StatusProgressBarProps = {
  value: number;
  total: number;
};

export function StatusProgressBar({ value, total }: StatusProgressBarProps) {
  const percentage = total ? Math.min(Math.max((value / total) * 100, 0), 100) : 0;

  return (
    <div className="mt-2 h-2 rounded-full bg-gray-100">
      <div
        className="h-2 rounded-full bg-blue-500"
        style={{ width: `${percentage}%` }}
        aria-valuenow={percentage}
        aria-valuemin={0}
        aria-valuemax={100}
      />
    </div>
  );
}

