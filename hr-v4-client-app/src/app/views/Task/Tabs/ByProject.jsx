  import React from 'react'
  import { Grid, Collapse,Button } from "@material-ui/core";
  import { useStore } from '../../../stores';
  import { observer } from 'mobx-react';
  import ArrowDropDownIcon from "@material-ui/icons/ArrowDropDown";
  import ArrowRightIcon from "@material-ui/icons/ArrowRight";
  import DescriptionIcon from "@material-ui/icons/Description";
  import TaskForm from '../TaskForm';

  export default observer(function ByProject() {

    const { taskStore } = useStore();
    const [visible, setVisible] = React.useState(-1);
    const { listTask, getAllTask,handleOpenPopupForm } = taskStore;
    React.useEffect(() => {
      getAllTask(4);
    }, []);

    return (
      <div className="content-index">
        <Grid className="index-card" style={{ marginTop: 10 }} >
          <Grid item xs={12} className="kanban-container">
            {listTask?.listProject.length > 0 && listTask?.listProject.map((project, index) => (
              <> {project.listItem.length > 0 ?
              <div key={project.id} style={{display:"flex"}}>
                <Button onClick={() => {setVisible(visible === index ? -1 : index)}} style={{margin:"10px",maxHeight:"30px"}} >
                    {visible === index ? <ArrowDropDownIcon  /> :<ArrowRightIcon   />}
                </Button> 
                <div style={{borderBottom:"1.5px solid #00000036",display:"unset"}}>
                  <p>
                  <span className={`tag `} style={{backgroundColor:"rgb(221 143 237)"}} >{project.name}</span> {project.listItem.length}
                  </p>
                  <Collapse in={visible === index}>
                  {project.listItem.map((item) => (
                    <div key={item.id} style={{display:"flex",margin:"10px 0px"}} onClick={() => handleOpenPopupForm(item.id)}><DescriptionIcon  /> 
                      <div style={{fontSize:"18px",textDecoration:"underline rgba(170, 170, 170, 0.42)",width:"100%"}} >{item?.name}</div>
                      <div >
                      {item?.status?.name === "Testing" ? <span className="tag bgc-orange" style={{float:"right",width:"100%"}}>{item?.status?.name}</span>
                        : item?.status?.name === "On Hold" ? <span className="tag bgc-brown" style={{float:"right",width:"100%"}}>{item?.status?.name}</span>
                        : item?.status?.name === "Dev Done" ? <span className="tag bgc-success" style={{float:"right",width:"100%"}}>{item?.status?.name}</span>
                        : item?.status?.name === "New" ? <span className="tag bgc-primary" style={{float:"right",width:"100%"}}>{item?.status?.name}</span>
                        : item?.status?.name === "Test Done" ? <span className="tag bgc-warning-d1" style={{float:"right",width:"100%"}}>{item?.status?.name}</span>
                        : item?.status?.name === "Coding" ? <span className="tag bgc-yellow" style={{float:"right",width:"100%"}}>{item?.status?.name}</span>
                        : item?.status?.name === "Completed" ? <span className="tag bgc-danger-tp1" style={{float:"right",width:"100%"}}>{item?.status?.name}</span> : <></>}
                      </div> 
                    </div>
                    ))
                  }
                  </Collapse>
                </div>
              </div>:""}
              </>
            ))}
            <TaskForm />
          </Grid>
        </Grid>
      </div>
    )
  })