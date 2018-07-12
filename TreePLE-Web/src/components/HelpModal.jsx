import React, {PureComponent} from 'react';
import PropTypes from 'prop-types';
import {Button, Divider, Grid, Header, Icon, Modal} from 'semantic-ui-react';
import {iconDef, colorDef} from '../constants';

class HelpModal extends PureComponent {
  constructor(props) {
    super(props);
  }

  render() {
    let iconIter = [...Array(Math.ceil(iconDef.length/2)).keys()];
    let colorIter = [...Array(Math.ceil(colorDef.length/2)).keys()];

    return (
      <Modal open size='small' dimmer='blurring'>
        <Modal.Content>
          <Modal.Header>
            <Header as='h1' icon textAlign='center'>
              <Icon name='help' circular/>
              <Header.Content>Help</Header.Content>
            </Header>
          </Modal.Header>
          <Modal.Description>
            <Header as='h3' textAlign='center'>
              <Header.Content>Icons</Header.Content>
            </Header>
            <Grid columns={4}>
              <Grid.Row>
                <Grid.Column textAlign='center' width={2}>
                  <Header.Content as='h4'>Icon</Header.Content>
                </Grid.Column>
                <Grid.Column width={6}>
                  <Header.Content as='h4'>Meaning</Header.Content>
                </Grid.Column>
                <Grid.Column textAlign='center' width={2}>
                  <Header.Content as='h4'>Icon</Header.Content>
                </Grid.Column>
                <Grid.Column width={6}>
                  <Header.Content as='h4'>Meaning</Header.Content>
                </Grid.Column>
              </Grid.Row>
            </Grid>
            <Divider/>
            <Grid columns={4}>
              {iconIter.map((i) => (
                <Grid.Row key={i} verticalAlign='middle'>
                  <Grid.Column textAlign='center' width={2}>
                    <Icon name={iconDef[2*i].icon}/>
                  </Grid.Column>
                  <Grid.Column width={6}>
                    {iconDef[2*i].def}
                  </Grid.Column>
                  <Grid.Column textAlign='center' width={2}>
                    {2*i+1 in iconDef ? <Icon name={iconDef[2*i+1].icon}/> : null}
                  </Grid.Column>
                  <Grid.Column width={6}>
                    {2*i+1 in iconDef ? iconDef[2*i+1].def : null}
                  </Grid.Column>
                </Grid.Row>
              ))}
            </Grid>

            <Divider hidden/>

            <Header as='h3' textAlign='center'>
              <Header.Content>Colors</Header.Content>
            </Header>
            <Grid>
              <Grid.Row>
                <Grid.Column textAlign='center' width={2}>
                  <Header.Content as='h4'>Color</Header.Content>
                </Grid.Column>
                <Grid.Column>
                  <Header.Content as='h4'>Meaning</Header.Content>
                </Grid.Column>
              </Grid.Row>
            </Grid>
            <Divider/>
            <Grid columns={2}>
              {colorIter.map((i) => (
                <Grid.Row key={i} verticalAlign='middle'>
                  <Grid.Column textAlign='center' width={2}>
                    <span style={{color: colorDef[2*i].color}}>{colorDef[2*i].color.toUpperCase()}</span>
                  </Grid.Column>
                  <Grid.Column width={6}>
                    {colorDef[2*i].def}
                  </Grid.Column>
                  <Grid.Column textAlign='center' width={2}>
                    {2*i+1 in colorDef ? <span style={{color: colorDef[2*i+1].color}}>{colorDef[2*i+1].color.toUpperCase()}</span> : null}
                  </Grid.Column>
                  <Grid.Column width={6}>
                    {2*i+1 in colorDef ? colorDef[2*i+1].def : null}
                  </Grid.Column>
                </Grid.Row>
              ))}
            </Grid>

            <Divider hidden/>

            <Grid centered>
              <Grid.Row>
                <Button inverted color='red' size='small' onClick={this.props.onClose}>Close</Button>
              </Grid.Row>
            </Grid>
          </Modal.Description>
        </Modal.Content>
      </Modal>
    );
  }
}

HelpModal.propTypes = {
  onClose: PropTypes.func.isRequired
};

export default HelpModal;
