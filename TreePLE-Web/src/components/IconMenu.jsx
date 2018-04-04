import React, {PureComponent} from 'react';
import {Sidebar, Segment, Button, Menu, Image, Icon, Header} from 'semantic-ui-react';

class IconMenu extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      visible: false
    };
  }

  handleItemClick = (e, {name}) => this.setState({ activeItem: name })

  render() {
    const {activeItem} = this.state

    return (
      <Menu stackable>
        {/* <Menu.Item>
          <img src='/logo.png' />
        </Menu.Item> */}

        <Menu.Item
          name='features'
          active={activeItem === 'features'}
          onClick={this.handleItemClick}
        >
          Features
        </Menu.Item>

        <Menu.Item
          name='testimonials'
          active={activeItem === 'testimonials'}
          onClick={this.handleItemClick}
        >
          Testimonials
        </Menu.Item>

        <Menu.Item
          name='sign-in'
          active={activeItem === 'sign-in'}
          onClick={this.handleItemClick}
        >
          Sign-in
        </Menu.Item>
      </Menu>
    )
  }
}

// IconMenu.propTypes = {
// }

export default IconMenu;