import React, {PureComponent} from 'react';
import {Link} from 'react-router-dom';
import {Button, Input, Image, Modal, Label, Form} from 'semantic-ui-react'

const options = [
  { key: 'R', text: 'Resident', value: 'resident' },
  { key: '', text: 'Scientist', value: 'scientist' },
]

class About extends PureComponent {
  render () {
    return (
      <div>
        <div>
          About
          <Link to="/">
            <button>Go Home</button>
          </Link>
        </div>
        <div>

        <Modal trigger={<Button>Login</Button>} basic size='small'>
        
         
            <Modal.Content image>
            
            <div>
              <Image src='images/favicon.ico' size='medium' spaced = 'true|right' />
              </div>
              <Modal.Description>
                
              <Form>

              
              <Form.Input fluid label='Username' placeholder='Username' />
              
              <Form.Input type = 'password' fluid label='Password' placeholder='Password' />

              <Form.Button color = 'olive' size = 'massive' >Sign In</Form.Button>
              
              </Form>
                
              </Modal.Description>
            </Modal.Content>
          </Modal>
          
        </div>
      

        <div>
          <Modal trigger= {<Button> Register</Button>} basic size = "small">
          
            <Modal.Content image>
            <div>
             <Image src='images/favicon.ico' size='medium' spaced = 'true|right' />  
             </div>
              <Modal.Description>
              <Form>

              
              
              <Form.Input fluid label='Username' placeholder='Username' />
              
              <Form.Input type = 'password' fluid label='Password' placeholder='Password' />

              <Form.Input type = 'password' fluid label='Password2' placeholder='Confirm Password' />

              <Form.Select fluid label='Role' options={options} placeholder='Role' />

              <Form.Input type = 'password' fluid label='ScientistAccess' placeholder='Scientist Access' />

              <Form.Input fluid label='PostalCode' placeholder='PostalCode' />

              <Form.Button color = 'olive' size = 'massive' >Register</Form.Button>

              </Form>
              </Modal.Description>
            </Modal.Content>
          </Modal>
        </div>
      </div>
    );
  };
};

export default About;