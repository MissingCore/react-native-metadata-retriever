import type { TextProps as RNTextProps } from 'react-native';
import { StyleSheet, Text as RNText } from 'react-native';

type TextProps = RNTextProps & { variant?: 'heading' | 'center' };

export function Text({ style, variant, ...props }: TextProps) {
  return (
    <RNText
      // eslint-disable-next-line react-native/no-inline-styles
      style={[{ color: 'black' }, variant ? styles[variant] : undefined, style]}
      {...props}
    />
  );
}

const styles = StyleSheet.create({
  heading: {
    marginHorizontal: 16,
    textAlign: 'center',
    fontWeight: 'bold',
  },
  center: {
    marginHorizontal: 16,
    textAlign: 'center',
  },
});
