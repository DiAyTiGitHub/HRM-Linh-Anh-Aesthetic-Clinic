import React from 'react'
import { useStore } from "../../../stores";
import { useEffect } from 'react';
import { useState } from 'react';
import TaskGroup from '../ViewTask/Group/TaskGroup';
import { RenderListButtonView } from '../TaskIndex';

export default function AllTask() {

  const { getAllTask } = useStore().taskStore;

  const [views, setViews] = useState('group');


  useEffect(() => {
    getAllTask()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <>
      <RenderListButtonView view={views} setView={setViews} />
      <TaskGroup />
    </>
  )
}

