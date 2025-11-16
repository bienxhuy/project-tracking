import { Outlet } from 'react-router-dom'
const MainLayout = () => {
  return (
    <>
      <h1 className='w-full h-60[px] text-center'>HEADER</h1>
      <Outlet />
      <h1 className='w-full h-60[px] text-center'>FOOTER</h1>
    </>
  )
}

export default MainLayout