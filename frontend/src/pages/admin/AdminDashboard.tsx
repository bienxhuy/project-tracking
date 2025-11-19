import { Card, CardContent } from "@/components/ui/card";

export function AdminDashboard() {
  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div>
        <h2 className="text-3xl font-bold text-gray-900">Dashboard</h2>
        <p className="text-gray-600 mt-2">
          Overview of your system statistics and activity
        </p>
      </div>

      {/* Coming Soon Notice */}
      <Card className="bg-gradient-to-r from-blue-50 to-purple-50 border-blue-200">
        <CardContent className="pt-6">
          <div className="text-center">
            <h3 className="text-lg font-semibold text-gray-900">🚀 More Features Coming Soon</h3>
            <p className="text-sm text-gray-600 mt-2">
              Dashboard analytics, project management, and advanced reporting features are under development.
            </p>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}

