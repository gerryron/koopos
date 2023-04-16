import Head from 'next/head';

function Dashboard() {
  return (
    <div className="flex justify-center items-center h-screen bg-gray-100">
      <Head>
        <title>Dashboard</title>
      </Head>
      <div className="w-full max-w-md p-6 bg-white rounded-lg shadow-md">
        <h1 className="text-3xl font-semibold mb-4">Dashboard</h1>
        <p>Welcome to your dashboard!</p>
      </div>
    </div>
  );
}

export default Dashboard;
