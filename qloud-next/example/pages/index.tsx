import Head from "next/head";
import { LOGOUT_PATH, qloud } from "../lib/qloud";
import { GetServerSideProps, NextPage } from "next";
import { AuthOrNull } from "@qloud/next";
import { useEffect, useState } from "react";
import { UserData } from "./api/user";

type HomeProps = AuthOrNull;

const Home: NextPage<HomeProps> = function Home({ auth }: HomeProps) {
  const emailFromApi = useFetchEmailFromApi();

  return (
    <div style={{ maxWidth: "980px", margin: "auto" }}>
      <Head>
        <title>Qloud Next</title>
      </Head>

      <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between" }}>
        <h1>Qloud Next.js</h1>
        <button
          type="button"
          onClick={() => (document.location.href = LOGOUT_PATH)}
          style={{ fontSize: "1.3rem", padding: "8px" }}
        >
          Logout
        </button>
      </div>

      <ul>
        <li>E-Mail from server-side props: {auth?.email}</li>
        <li>E-Mail from API: {emailFromApi}</li>
      </ul>
    </div>
  );
};

function useFetchEmailFromApi(): string | null {
  const [emailFromApi, setEmailFromApi] = useState<string | null>(null);

  useEffect(() => {
    fetch("/api/user").then(async (response) => {
      const responseBody = (await response.json()) as UserData;
      setEmailFromApi(responseBody.email);
    });
  });
  return emailFromApi;
}

export const getServerSideProps: GetServerSideProps<HomeProps> = async (context) => {
  return qloud.getServerSideProps(context);
};

export default Home;
