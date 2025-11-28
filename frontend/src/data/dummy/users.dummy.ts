// Dummy data for user management testing - matching backend entity structure

import { User, UserStats } from "@/types/user.type";
import { UserRole, UserStatus, LoginType } from "@/types/util.type";

export const dummyUsers: User[] = [
  {
    id: 1,
    username: "nguyenvanan",
    email: "nguyen.van.an@ute.edu.vn",
    displayName: "Nguyễn Văn An",
    role: UserRole.STUDENT,
    accountStatus: UserStatus.ACTIVE,
    level: 5.5,
    loginType: LoginType.LOCAL,
    avatar: "https://api.dicebear.com/7.x/avataaars/svg?seed=An",
    createdAt: "2024-01-15T08:00:00",
    updatedAt: "2024-03-20T10:30:00"
  },
  {
    id: 2,
    username: "tranthibinh",
    email: "tran.thi.binh@ute.edu.vn",
    displayName: "Trần Thị Bình",
    role: UserRole.INSTRUCTOR,
    accountStatus: UserStatus.ACTIVE,
    level: 8.2,
    loginType: LoginType.GOOGLE,
    avatar: "https://api.dicebear.com/7.x/avataaars/svg?seed=Binh",
    createdAt: "2024-01-16T08:00:00",
    updatedAt: "2024-03-19T14:20:00"
  },
  {
    id: 3,
    username: "lehoangcuong",
    email: "le.hoang.cuong@ute.edu.vn",
    displayName: "Lê Hoàng Cường",
    role: UserRole.STUDENT,
    accountStatus: UserStatus.ACTIVE,
    level: 6.8,
    loginType: LoginType.LOCAL,
    avatar: "https://api.dicebear.com/7.x/avataaars/svg?seed=Cuong",
    createdAt: "2024-01-17T08:00:00",
    updatedAt: "2024-03-21T11:10:00"
  },
  {
    id: 4,
    username: "phamthidung",
    email: "pham.thi.dung@ute.edu.vn",
    displayName: "Phạm Thị Dung",
    role: UserRole.STUDENT,
    accountStatus: UserStatus.INACTIVE,
    level: 3.5,
    loginType: LoginType.LOCAL,
    avatar: "https://api.dicebear.com/7.x/avataaars/svg?seed=Dung",
    createdAt: "2024-01-18T08:00:00",
    updatedAt: "2024-02-20T16:00:00"
  },
  {
    id: 5,
    username: "hoangvanem",
    email: "hoang.van.em@ute.edu.vn",
    displayName: "Hoàng Văn Em",
    role: UserRole.STUDENT,
    accountStatus: UserStatus.ACTIVE,
    level: 7.0,
    loginType: LoginType.GOOGLE,
    avatar: "https://api.dicebear.com/7.x/avataaars/svg?seed=Em",
    createdAt: "2024-01-19T08:00:00",
    updatedAt: "2024-03-20T15:45:00"
  },
  {
    id: 6,
    username: "admin_tuan",
    email: "nguyen.minh.tuan@ute.edu.vn",
    displayName: "Dr. Nguyễn Minh Tuấn",
    role: UserRole.ADMIN,
    accountStatus: UserStatus.ACTIVE,
    level: 10.0,
    loginType: LoginType.LOCAL,
    avatar: "https://api.dicebear.com/7.x/avataaars/svg?seed=Tuan",
    createdAt: "2023-08-01T08:00:00",
    updatedAt: "2024-03-21T09:00:00"
  },
  {
    id: 7,
    username: "instructor_huong",
    email: "tran.thi.huong@ute.edu.vn",
    displayName: "Trần Thị Hương",
    role: UserRole.INSTRUCTOR,
    accountStatus: UserStatus.ACTIVE,
    level: 9.5,
    loginType: LoginType.LOCAL,
    avatar: "https://api.dicebear.com/7.x/avataaars/svg?seed=Huong",
    createdAt: "2023-08-01T08:00:00",
    updatedAt: "2024-03-20T17:30:00"
  },
  {
    id: 8,
    username: "vothanhhai",
    email: "vo.thanh.hai@ute.edu.vn",
    displayName: "Võ Thanh Hải",
    role: UserRole.STUDENT,
    accountStatus: UserStatus.VERIFYING,
    level: 4.5,
    loginType: LoginType.GOOGLE,
    avatar: "https://api.dicebear.com/7.x/avataaars/svg?seed=Hai",
    createdAt: "2024-01-20T08:00:00",
    updatedAt: "2024-03-21T12:00:00"
  },
  {
    id: 9,
    username: "dangthilan",
    email: "dang.thi.lan@ute.edu.vn",
    displayName: "Đặng Thị Lan",
    role: UserRole.INSTRUCTOR,
    accountStatus: UserStatus.ACTIVE,
    level: 7.8,
    loginType: LoginType.LOCAL,
    avatar: "https://api.dicebear.com/7.x/avataaars/svg?seed=Lan",
    createdAt: "2024-01-21T08:00:00",
    updatedAt: "2024-03-19T10:15:00"
  },
  {
    id: 10,
    username: "buivankhoa",
    email: "bui.van.khoa@ute.edu.vn",
    displayName: "Bùi Văn Khoa",
    role: UserRole.STUDENT,
    accountStatus: UserStatus.ACTIVE,
    level: 2.0,
    loginType: LoginType.LOCAL,
    avatar: "https://api.dicebear.com/7.x/avataaars/svg?seed=Khoa",
    createdAt: "2024-01-22T08:00:00",
    updatedAt: "2024-02-28T14:00:00"
  },
  {
    id: 11,
    username: "admin_nam",
    email: "le.van.nam@ute.edu.vn",
    displayName: "Dr. Lê Văn Nam",
    role: UserRole.ADMIN,
    accountStatus: UserStatus.ACTIVE,
    level: 10.0,
    loginType: LoginType.LOCAL,
    avatar: "https://api.dicebear.com/7.x/avataaars/svg?seed=Nam",
    createdAt: "2023-08-15T08:00:00",
    updatedAt: "2024-03-21T08:30:00"
  },
  {
    id: 12,
    username: "ngothimai",
    email: "ngo.thi.mai@ute.edu.vn",
    displayName: "Ngô Thị Mai",
    role: UserRole.STUDENT,
    accountStatus: UserStatus.ACTIVE,
    level: 6.2,
    loginType: LoginType.GOOGLE,
    avatar: "https://api.dicebear.com/7.x/avataaars/svg?seed=Mai",
    createdAt: "2024-01-23T08:00:00",
    updatedAt: "2024-03-20T13:20:00"
  },
  {
    id: 13,
    username: "tranquanghieu",
    email: "tran.quang.hieu@ute.edu.vn",
    displayName: "Trần Quang Hiếu",
    role: UserRole.INSTRUCTOR,
    accountStatus: UserStatus.VERIFYING,
    level: 5.0,
    loginType: LoginType.GOOGLE,
    avatar: "https://api.dicebear.com/7.x/avataaars/svg?seed=Hieu",
    createdAt: "2024-02-01T08:00:00",
    updatedAt: "2024-03-15T09:00:00"
  },
  {
    id: 14,
    username: "phanvantoan",
    email: "phan.van.toan@ute.edu.vn",
    displayName: "Phan Văn Toàn",
    role: UserRole.STUDENT,
    accountStatus: UserStatus.ACTIVE,
    level: 7.5,
    loginType: LoginType.LOCAL,
    avatar: "https://api.dicebear.com/7.x/avataaars/svg?seed=Toan",
    createdAt: "2024-02-05T08:00:00",
    updatedAt: "2024-03-18T16:20:00"
  },
  {
    id: 15,
    username: "vuthiyen",
    email: "vu.thi.yen@ute.edu.vn",
    displayName: "Vũ Thị Yến",
    role: UserRole.STUDENT,
    accountStatus: UserStatus.ACTIVE,
    level: 1.5,
    loginType: LoginType.LOCAL,
    avatar: "https://api.dicebear.com/7.x/avataaars/svg?seed=Yen",
    createdAt: "2024-01-10T08:00:00",
    updatedAt: "2024-03-05T10:00:00"
  }
];

export const dummyUserStats: UserStats = {
  totalUsers: 15,
  totalAdmins: 2,
  totalInstructors: 4,
  totalStudents: 9,
  totalInactive: 1
};
// Simulated user data from BE
