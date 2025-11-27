import { Card, CardContent, CardHeader } from "@/components/ui/card";

export function DashboardSkeleton() {
  return (
    <div className="space-y-6">
      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        {[...Array(4)].map((_, index) => (
          <Card key={index} className="animate-pulse">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <div className="h-4 w-24 rounded bg-gray-200" />
              <div className="h-8 w-8 rounded-lg bg-gray-200" />
            </CardHeader>
            <CardContent>
              <div className="h-8 w-16 rounded bg-gray-200" />
              <div className="mt-2 h-3 w-32 rounded bg-gray-100" />
            </CardContent>
          </Card>
        ))}
      </div>
      <Card className="animate-pulse">
        <CardContent className="h-48" />
      </Card>
      <div className="grid gap-6 lg:grid-cols-2">
        <Card className="animate-pulse">
          <CardContent className="h-64" />
        </Card>
        <Card className="animate-pulse">
          <CardContent className="h-64" />
        </Card>
      </div>
    </div>
  );
}

