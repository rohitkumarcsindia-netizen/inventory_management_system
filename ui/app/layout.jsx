import { Geist, Geist_Mono } from "next/font/google";
import "./globals.css";
import MainLayout from "../components/layout/MainLayout";


const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata = {
  title: "CyanConnode",
  icons: {
    icon: "/icon.png",
  },
};




export default function RootLayout({ children }) {
  return (
    <html lang="en">
      <body>
        <MainLayout>{children}</MainLayout>
      </body>
    </html>
  );
}
