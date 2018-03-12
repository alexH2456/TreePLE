import React, {PureComponent} from 'react';
import {Link} from 'react-router-dom';
import {Sidebar, Segment, Button, Menu, Image, Icon, Header} from 'semantic-ui-react';
import App from './App';

class NavigationBar extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      visible: false
    };
  }

  toggleVisibility = () => this.setState({visible: !this.state.visible});

  render() {
    const {visible} = this.state;
    return (
      <div>
        <Button size='small' onClick={this.toggleVisibility}>
          <Image src='../../images/favicon.ico' width='20%'/>
        </Button>
        <div>
          <Sidebar.Pushable as={Segment}>
            <Sidebar as={Menu} animation='overlay' direction='bottom' visible={visible} inverted>
              <Menu.Item name='home'>
                <Icon name='home'/>
                Home
              </Menu.Item>
              <Menu.Item name='gamepad'>
                <Icon name='gamepad'/>
                Games
              </Menu.Item>
              <Menu.Item name='camera'>
                <Icon name='camera'/>
                Channels
              </Menu.Item>
            </Sidebar>
            <Sidebar.Pusher>
              <Segment basic>
                <App/>
              </Segment>
            </Sidebar.Pusher>
          </Sidebar.Pushable>
        </div>
      </div>
    );
  }
}

export default NavigationBar;